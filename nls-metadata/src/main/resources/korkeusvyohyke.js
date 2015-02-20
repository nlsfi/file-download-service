/**
 * Korkeusvyöhykerasteri
 **/

datasetHandler.addDatasetDefinition({
    datasetName: 'korkeusvyohyke',
    fileIdentifier: '5c2ba253-e1b0-42c8-b9bb-3bac947e1cf1',
    pattern: /\/tuotteet\/korkeusvyohyke\/(\w*)\/(\w*)\/(\w*)(?:\/[A-Z][0-9]{1,2}){0,2}\/(\w*)\w{4}.png$/i,
    mappers: [
        defaultMapper(MetadataProperty.NLS_DATASETVERSION),
        lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
        lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings),
        function(value, outputProperties) {
            // special case for 640m (KM10_S640.png) where the whole finland is in one file
            // and thus no gridcell id exists
            if (value.indexOf("_") < 0) {
                outputProperties.put(MetadataProperty.NLS_GRIDCELL, value);
            }
        }
    ],
    callback: function(node, outputProperties) {
        var arr = getWorldFile(node, outputProperties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        outputProperties.put(MetadataProperty.NLS_RELATED, arr);
    }
});