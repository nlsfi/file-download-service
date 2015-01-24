/**
 * Kiinteistörekisterikartta
 *
 **/
datasetHandler.addDatasetDefinition({
	datasetName : 'kiinteistorekisterikartta',
	fileIdentifier : '472b3e52-5ba8-4967-8785-4fa13955b42e',
	pattern : /\/tuotteet\/kiinteistorekisterikartta\/kayttorajattu\/(\S*)\/(\S*)\/(\S*)\/\S*\/(\S*).zip/i,
	mappers : [
	   defaultMapper(MetadataProperty.NLS_DATASETVERSION),
       lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
       lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings),
       defaultMapper(MetadataProperty.NLS_GRIDCELL)
	]
});
