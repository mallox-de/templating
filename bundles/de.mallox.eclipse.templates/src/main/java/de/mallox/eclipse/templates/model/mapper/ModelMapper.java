package de.mallox.eclipse.templates.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import de.mallox.eclipse.templates.model.AttributeEntry;
import de.mallox.eclipse.templates.model.BuildCommandEntry;
import de.mallox.eclipse.templates.model.ClasspathentryEntry;
import de.mallox.eclipse.templates.model.FileEntry;
import de.mallox.eclipse.templates.model.ProjectDescriptionEntry;
import de.mallox.eclipse.templates.model.ProjectTemplateEntry;
import de.mallox.eclipse.templates.model.TemplateEntry;
import de.mallox.eclipse.templates.model.TemplateIndex;
import de.mallox.eclipse.templates.model.TemplateIndexEntry;
import de.mallox.eclipse.templates.model.TemplateRef;
import de.mallox.eclipse.templates.model.TemplateRefEntry;
import de.mallox.eclipse.templates.model.VariableCombinationEntry;
import de.mallox.eclipse.templates.model.VariableEntry;
import de.mallox.eclipse.templates.model.template.Attribute;
import de.mallox.eclipse.templates.model.template.BuildCommand;
import de.mallox.eclipse.templates.model.template.Classpathentry;
import de.mallox.eclipse.templates.model.template.File;
import de.mallox.eclipse.templates.model.template.ProjectDescription;
import de.mallox.eclipse.templates.model.template.ProjectTemplate;
import de.mallox.eclipse.templates.model.template.Template;
import de.mallox.eclipse.templates.model.template.Variable;
import de.mallox.eclipse.templates.model.template.VariableCombination;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ModelMapper {

	ModelMapper INSTANCE = Mappers.getMapper(ModelMapper.class);

	AttributeEntry mapToInternalModel(Attribute pAttribute);

	BuildCommandEntry mapToInternalModel(BuildCommand pBuildCommand);

	@Mapping(target = "resolvedPath", ignore = true)
	ClasspathentryEntry mapToInternalModel(Classpathentry pClasspathentry);

	@Mappings({ @Mapping(target = "resolvedDest", ignore = true),
			@Mapping(target = "resolvedCondition", ignore = true) })
	FileEntry mapToInternalModel(File pFile);

	@Mapping(target = "resolvedProjectDependencies", ignore = true)
	ProjectDescriptionEntry mapToInternalModel(ProjectDescription pProjectDescription);

	@Mappings({ @Mapping(target = "resolvedLauncher", ignore = true), @Mapping(target = "resolvedName", ignore = true),
			@Mapping(target = "resolvedCondition", ignore = true) })
	ProjectTemplateEntry mapToInternalModel(ProjectTemplate pProjectTemplate);

	@Mappings({ @Mapping(target = "instance", ignore = true) })
	de.mallox.eclipse.templates.model.VelocityTool mapToInternalModel(
			de.mallox.eclipse.templates.model.template.VelocityTool pVelocityTool);

	TemplateEntry mapToInternalModel(Template pTemplate);

	VariableCombinationEntry mapToInternalModel(VariableCombination pVariableCombination);

	@Mapping(target = "value", ignore = true)
	VariableEntry mapToInternalModel(Variable pVariable);

	TemplateIndexEntry mapToInternalModel(TemplateIndex pTemplateIndex);

	@Mappings({ @Mapping(target = "description", ignore = true),
			@Mapping(target = "generatorVersionCompatibleWithTemplate", ignore = true) })
	TemplateRefEntry mapToInternalModel(TemplateRef pTemplateRef);
}
