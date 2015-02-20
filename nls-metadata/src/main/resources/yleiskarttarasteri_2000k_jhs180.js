/**
 * Yleiskarttarasteri 1:2 000 000
 **/

datasetHandler.addDatasetDefinition({
    datasetName: 'yleiskarttarasteri_2000k_jhs180',
    fileIdentifier: 'b726f2c9-992f-41af-8bb5-c669405be724',
    pattern: /\/tuotteet\/yleiskarttarasteri_2000k_jhs180\/(\S*)\/(\S*)\/(\S*)\/\S*.png/i,
    mappers: [
        defaultMapper(MetadataProperty.NLS_DATASETVERSION),
        lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
        lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings)
    ],
    callback: function(node, outputProperties) {
        var arr = getWorldFile(node, outputProperties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        outputProperties.put(MetadataProperty.NLS_RELATED, arr);
    }
});