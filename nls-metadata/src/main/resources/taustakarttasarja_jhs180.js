/**
 * Taustakarttasarja
 **/

datasetHandler.addDatasetDefinition({
    datasetName: 'taustakarttasarja',
    fileIdentifier: 'taustakarttasarja_jhs180',
    pattern: /\/tuotteet\/taustakarttasarja_jhs180\/(\w*)\/\w*\/(\w*)\/(\w*)\/(?:\w*\/){0,2}(?:\d*|(\w*)).png/i,
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