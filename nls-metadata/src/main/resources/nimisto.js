/**
 * Nimistö
 **/

datasetHandler.addDatasetDefinition({
    datasetName: 'nimisto',
    fileIdentifier: 'eec8a276-a406-4b0a-8896-741cd716ade6',
    pattern: /\/tuotteet\/nimisto\/(\S*)\/(\S*)\/(\S*)\/\S*_(\S*)_[0-9]{2}.zip/i,
    mappers: [
        defaultMapper(MetadataProperty.NLS_DATASETVERSION),
        lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
        lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings),
        defaultMapper(MetadataProperty.NLS_YEAR)
    ]
});