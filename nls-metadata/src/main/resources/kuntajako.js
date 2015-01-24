/**
 * Kuntajako
 **/

datasetHandler.addDatasetDefinition({
	datasetName : 'kuntajako',
	fileIdentifier : 'da40f862-44b5-47b9-aea8-83bb1e640ca9',
	pattern : /\/tuotteet\/kuntajako\/(\S*)\/(\S*)\/(\S*)\/[a-z][A-Z]*_(\d*)_\S*.zip/i,
	mappers : [
	   defaultMapper(MetadataProperty.NLS_DATASETVERSION),
       lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
       lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings),
       defaultMapper(MetadataProperty.NLS_YEAR)
	]
});
