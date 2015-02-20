/* 
 * Maastokarttarasteri 50k
 */

datasetHandler.addDatasetDefinition({
    datasetName: 'maastokarttarasteri_250k_jhs180',
    fileIdentifier: '924a68ba-665f-4ea0-a830-26e80112b5dc',
    pattern: /\/tuotteet\/maastokarttarasteri_250k_jhs180\/(\S*)\/(\S*)\/(\S*)\/[A-Z][0-9]\/[A-Z][0-9][L|R]\/(\S*).png/i,
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