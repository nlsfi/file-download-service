/**
 * Maastotietokanta
 **/

datasetHandler.addDatasetDefinition({
    datasetName : 'maastotietokanta',
    fileIdentifier : 'cfe54093-aa87-46e2-bfa2-a20def7b036f',
    pattern : /\/tuotteet\/maastotietokanta\/(\S*)\/(\S*)\/(\S*)\/\S*\/\S+\/(\S*?)(?:|.mif|.shp|_mtk).zip/i,
    mappers : [
        defaultMapper(MetadataProperty.NLS_DATASETVERSION),
        lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
        lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings),
        defaultMapper(MetadataProperty.NLS_GRIDCELL)
    ]
});

// Tiestö osoitteilla tuoteversio
datasetHandler.addDatasetDefinition({
    datasetName : 'maastotietokanta',
    fileIdentifier : 'cfe54093-aa87-46e2-bfa2-a20def7b036f',
    pattern : /\/tuotteet\/maastotietokanta\/tiesto_osoitteilla\/(\S*)\/(\S*)\/([A-Z][0-9]*)(?:.|_)?\S*.zip/i,
    mappers : [
        lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
        lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings),
        defaultMapper(MetadataProperty.NLS_GRIDCELL)
    ],
    callback : function(node, outputProperties) {
    	outputProperties.put(MetadataProperty.NLS_DATASETVERSION, "tiesto_osoitteilla");
    }
});
