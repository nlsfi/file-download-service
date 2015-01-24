/* 
 * Maastokarttarasteri 250k
 */

datasetHandler.addDatasetDefinition({
	datasetName : 'maastokarttarasteri_250k',
	fileIdentifier : '924a68ba-665f-4ea0-a830-26e80112b5dc',
	pattern : /\/tuotteet\/maastokarttarasteri_250k\/(\S*)\/(\S*)\/(\S*)\/[A-Z][0-9]\/U([a-zA-Z][0-9]*[LR])_RVK.tif/i,
	mappers : [
	   defaultMapper(MetadataProperty.NLS_DATASETVERSION),
       lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
       lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings),
       defaultMapper(MetadataProperty.NLS_GRIDCELL)
	],
	callback : function(node, outputProperties) {
		var arr = getWorldFile(node, outputProperties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
		outputProperties.put(MetadataProperty.NLS_RELATED, arr);
	}
});
