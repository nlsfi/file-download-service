/* 
 * Maastokarttarasteri 500k
 */

datasetHandler.addDatasetDefinition({
    datasetName: 'maastokarttarasteri_500k_jhs180',
    fileIdentifier: '274dc94e-8dd7-4ab5-959d-70278b23c12d',
    pattern: /\/tuotteet\/maastokarttarasteri_500k_jhs180\/(\S*)\/(\S*)\/(\S*)\/(\S*).png/i,
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