/**
 * Peruskarttarasteri
 **/

datasetHandler.addDatasetDefinition({
    datasetName: 'peruskarttarasteri',
    fileIdentifier: 'c6e94f34-4925-4fa6-bac9-6b25f4e7cebf',
    pattern: /\/tuotteet\/peruskarttarasteri\/(\S*)\/\S*\/(\S*)\/(\S*)\/\S*\/\S*\/U([a-zA-Z][0-9]*[LR])_\S*.tif/i,
    mappers: [
        defaultMapper(MetadataProperty.NLS_DATASETVERSION),
        lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
        lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings),
        defaultMapper(MetadataProperty.NLS_GRIDCELL)
    ],
    callback: function(node, outputProperties) {
        var arr = getWorldFile(node, outputProperties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        outputProperties.put(MetadataProperty.NLS_RELATED, arr);
    }
});