/**
 * Yleiskartta 1:4 500 000
 **/

datasetHandler.addDatasetDefinition({
    datasetName: 'yleiskartta_4500k',
    fileIdentifier: '95175ec9-0f91-42ca-abca-f4f4359490d3',
    pattern: /\/tuotteet\/yleiskartta_4500k\/(\S*)\/(\S*)\/(\S*)\/\S*.zip/i,
    mappers: [
        defaultMapper(MetadataProperty.NLS_DATASETVERSION),
        lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
        lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings)
    ]
});