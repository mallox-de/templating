#[[# Changelog

New entries should always be added before the old one.

##]]# Project $ProjectTemplate.getResolvedName() is based on $Template.getName() $Template.getVersion() created at ${date} with GeneratorPluginVersion ${GeneratorPluginVersion}.

$ProjectTemplate.getDescription()

Used Variables:

#foreach( $Variable in $Template.getVariable() )
- Variable $Variable.getName() = '$Variable.getValue()'.
#end