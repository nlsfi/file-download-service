/* 
 * Maastokarttarasteri 50k
 */

datasetHandler.addDatasetDefinition({
    datasetName: 'maastokarttarasteri_50k_jhs180',
    fileIdentifier: '5c671d8d-be58-4f5d-8242-a150ecc82f95',
    pattern: /\/tuotteet\/maastokarttarasteri_50k_jhs180\/(\S*)\/\S*\/(\S*)\/(\S*)\/[A-Z][0-9]\/[A-Z][0-9][0-9]\/(\S*).png/i,
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