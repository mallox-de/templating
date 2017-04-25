package de.mallox.eclipse.templates.handler.template;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;

import de.mallox.eclipse.templates.handler.template.streamfilter.IStreamFilter;
import de.mallox.eclipse.templates.handler.template.streamfilter.JavaStreamFilter;
import de.mallox.eclipse.templates.handler.template.streamfilter.LauncherStreamFilter;
import de.mallox.eclipse.templates.handler.template.streamfilter.StreamFilterRegistry;
import de.mallox.eclipse.templates.handler.template.streamfilter.XmlStreamFilter;
import de.mallox.eclipse.templates.model.template.File;
import de.mallox.eclipse.templates.model.template.ProjectTemplate;
import de.mallox.eclipse.templates.model.template.Template;
import de.mallox.eclipse.templates.util.WorkspaceUtility;

public class CreateTemplateHandler {

	@SuppressWarnings("resource")
	public static void createTemplate(final IPath pDestinationPath, final List<IResource> pSelectedResources,
			final IProgressMonitor pMonitor) {
		final int tWorkAmount = pSelectedResources.size();
		pMonitor.beginTask("Copy files: ", tWorkAmount);

		final IWorkspaceRoot tWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IContainer tDestinationContainer = null;
		if (pDestinationPath.segmentCount() == 1) {
			tDestinationContainer = tWorkspaceRoot.getProject(pDestinationPath.lastSegment());
		} else {
			tDestinationContainer = tWorkspaceRoot.getFolder(pDestinationPath);
		}

		final StreamFilterRegistry tStreamFilterRegistry = new StreamFilterRegistry();
		tStreamFilterRegistry.add(new JavaStreamFilter());
		tStreamFilterRegistry.add(new LauncherStreamFilter());
		tStreamFilterRegistry.add(new XmlStreamFilter());

		final ProjectTemplateMap tProjectTemplateMap = new ProjectTemplateMap();
		for (final IResource tIResource : pSelectedResources) {
			if (tIResource instanceof IFile) {
				final IFile tSourceIFile = (IFile) tIResource;

				if (tSourceIFile.exists() && tSourceIFile.isAccessible()) {
					try (InputStream tSourceFileInputStream = tSourceIFile.getContents()) {
						String tEncoding = tSourceIFile.getCharset();
						final String tDestinationPath = tIResource.getFullPath().toString() + ".vm";
						final IFile tDestinationFile = tDestinationContainer.getFile(new Path(tDestinationPath));

						WorkspaceUtility.createParentFolder(tDestinationFile, new SubProgressMonitor(pMonitor, 0));

						InputStream tNewContentInputStream = tSourceFileInputStream;
						try {
							final IStreamFilter tFileHandler = tStreamFilterRegistry
									.getFileHandler(tSourceIFile.getFileExtension());
							if (tFileHandler != null) {
								tNewContentInputStream = tFileHandler.manipulate(tSourceIFile, tSourceFileInputStream, tEncoding);
							}
							tDestinationFile.create(tNewContentInputStream, true, new SubProgressMonitor(pMonitor, 1));
						} finally {
							if (tNewContentInputStream != null) {
								tNewContentInputStream.close();
							}
						}

						tDestinationFile.setCharset(tEncoding, new SubProgressMonitor(pMonitor, 1));
						tProjectTemplateMap.add(tSourceIFile.getProject(), tDestinationPath,
								tSourceIFile.getProjectRelativePath().toString(), tSourceIFile.getCharset());
					} catch (IOException | CoreException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			}
		}

		/*
		 * Create changelog-files and "global velocity library": TODO extract to
		 * something like add additional files
		 */
		for (final IProject tIProject : tProjectTemplateMap.getIprojects()) {
			writeChangeLogFile(tDestinationContainer, tIProject, pMonitor);
			writeGlobalVelocityLibrary(tDestinationContainer, tIProject, pMonitor);
		}

		Template tTemplate;
		try {
			tTemplate = tProjectTemplateMap.getTemplate();
		} catch (final CoreException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		/*
		 * Sort file list:
		 */
		for (ProjectTemplate tProjectTemplate : tTemplate.getProjectTemplate()) {
			List<File> tFiles = tProjectTemplate.getFiles();

			Collections.sort(tFiles, new Comparator<File>() {
				@Override
				public int compare(File pFile1, File pFile2) {
					if (pFile1 != null && pFile2 != null) {
						return pFile1.getSrc().compareTo(pFile2.getSrc());
					}
					return 0;
				}
			});
		}

		writeTemplateFile(tDestinationContainer, tTemplate, new SubProgressMonitor(pMonitor, 1));
	}

	private static void writeTemplateFile(final IContainer pDestinationContainer, final Template pTemplate,
			final IProgressMonitor pMonitor) {
		try {
			final JAXBContext tJaxbContext = JAXBContext.newInstance(Template.class);
			final Marshaller tMarshaller = tJaxbContext.createMarshaller();
			tMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			tMarshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF8");
			tMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					"http://www.mallox.de/schema/template template.xsd ");

			final StringWriter tWriter = new StringWriter();
			tMarshaller.marshal(pTemplate, tWriter);
			final ByteArrayInputStream tInputStream = new ByteArrayInputStream(tWriter.toString().getBytes());

			final IFile tTemplateFile = pDestinationContainer.getFile(new Path("template.xml"));
			tTemplateFile.create(tInputStream, false, pMonitor);

			try (final InputStream tResourceAsStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("/template.xsd")) {
				final IFile tTemplateXsdFile = pDestinationContainer.getFile(new Path("template.xsd"));
				tTemplateXsdFile.create(tResourceAsStream, false, pMonitor);
			} catch (final IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

		} catch (JAXBException | CoreException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static void writeChangeLogFile(final IContainer pDestinationContainer, final IProject pIProject,
			final IProgressMonitor pMonitor) {
		try (final InputStream tResourceAsStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("/changelog.md")) {

			final IFile tChangelogFile = pDestinationContainer
					.getFile(new Path(pIProject.getName() + "/changelog.md.vm"));
			tChangelogFile.create(tResourceAsStream, false, pMonitor);
		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static void writeGlobalVelocityLibrary(final IContainer pDestinationContainer, final IProject pIProject,
			final IProgressMonitor pMonitor) {
		try (final InputStream tResourceAsStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("/VM_global_library.vm")) {

			final IFile tGlobalVelocityLibraryFile = pDestinationContainer
					.getFile(new Path(pIProject.getName() + "/VM_global_library.vm"));
			tGlobalVelocityLibraryFile.create(tResourceAsStream, false, pMonitor);
		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
