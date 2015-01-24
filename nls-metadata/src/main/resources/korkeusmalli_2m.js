/**
 * Korkeusmalli 2m
 **/

datasetHandler.addDatasetDefinition({
	datasetName : 'korkeusmalli_2m',
	fileIdentifier : 'dd32d539-a8de-4c4e-aa44-523551ffec99',
	pattern : /\/tuotteet\/korkeusmalli_2m\/(\S*)\/(\S*)\/(\S*)\/[A-Z][0-9]\/[A-Z][0-9][0-9]\/(\S*).zip/i,
	mappers : [
	   defaultMapper(MetadataProperty.NLS_DATASETVERSION),
       lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
       lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings),
       defaultMapper(MetadataProperty.NLS_GRIDCELL)
	]
});
