/**
 * Karttalehtijako
 **/
datasetHandler.addDatasetDefinition({
	datasetName : 'karttalehtijako_ruudukko',
	pattern : /\/tuotteet\/karttalehtijako_ruudukko\/(\w*)\/(\w*)\/(\w*)\/(?:\w*).zip$/i,
	mappers : [
	   defaultMapper(MetadataProperty.NLS_DATASETVERSION),
       lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
       lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings)       
	]
});
