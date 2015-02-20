/**
 * Vinovalovarjosterasteri
 **/

datasetHandler.addDatasetDefinition({
    datasetName: 'vinovalovarjoste',
    fileIdentifier: '1f247c72-4487-4d20-9595-985560343066',
    pattern: /\/tuotteet\/vinovalovarjoste\/(\w*)\/(\w*)\/(\w*)(?:\/[A-Z][0-9]{1,2}){0,2}\/(\w*)\w{4}.png$/i,
    mappers: [
        defaultMapper(MetadataProperty.NLS_DATASETVERSION),
        lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
        lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings),
        function(value, outputProperties) {
            // special case for 640m (KM10_S640.png) where whole finland is in one file
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