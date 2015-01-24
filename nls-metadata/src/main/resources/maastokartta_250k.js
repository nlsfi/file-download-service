/**
 * Maastokartta 1:250 000
 **/

datasetHandler.addDatasetDefinition({
	datasetName : 'maastokartta_250k',
	fileIdentifier : 'a2cd4d67-ee20-47b7-b899-a4d72e72bb2d',
	pattern : /\/tuotteet\/maastokartta_250k\/(\S*)\/(\S*)\/(\S*)\/[A-Z][0-9]\/(\S*).zip/i,
	mappers : [
	   defaultMapper(MetadataProperty.NLS_DATASETVERSION),
       lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
       lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings),
       defaultMapper(MetadataProperty.NLS_GRIDCELL)
	]
});
