/**
 * Maastokartta 1:100 000
 **/

datasetHandler.addDatasetDefinition({
	datasetName : 'maastokartta_100k',
	fileIdentifier : 'e9861577-efd5-4448-aded-6131f9d14097',
	pattern : /\/tuotteet\/maastokartta_100k\/(\S*)\/(\S*)\/(\S*)\/[A-Z][0-9]\/(\S*).zip/i,
	mappers : [
	   defaultMapper(MetadataProperty.NLS_DATASETVERSION),
       lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
       lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings),
       defaultMapper(MetadataProperty.NLS_GRIDCELL)
	]
});
