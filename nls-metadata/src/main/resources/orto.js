/**
 * Maanmittauslaitoksen ortokuva
 **/

datasetHandler.addDatasetDefinition({
    datasetName: 'orto',
    fileIdentifier: 'b20a360b-1734-41e5-a5b8-0e90dd9f2af3',
    pattern: /\/tuotteet\/orto\/(\S*)\/(\S*)\/(\S*)\/[A-Z][0-9]{2}\/(\S{3})\/([0-9]+)\/(\S*:?)\.jp2/i,
    mappers: [
        lookupTableMapper("nls:crs", crsMappings),
        lookupTableMapper("nls:datasetVersion", {
            'mara_mv_20000_50': 'ortokuva',
            'mara_mv_35000_100': 'ortokuva',
            'mara_v_20000_50': 'ortokuva',
            'mara_v_25000_50': 'ortokuva',
            'mavi_v_25000_50': 'ortokuva',
            'mara_vv_20000_50': 'vaaravari_ortokuva',
            'mara_vv_25000_50': 'vaaravari_ortokuva',
            'mavi_vv_25000_50': 'vaaravari_ortokuva'
        }),
        defaultMapper(MetadataProperty.NLS_YEAROFPHOTOGRAPHY),
        defaultMapper(MetadataProperty.NLS_ELEVATIONMODEL),
        defaultMapper(MetadataProperty.NLS_FILEVERSION),
        defaultMapper(MetadataProperty.NLS_GRIDCELL)
    ],
    callback: function(node, outputProperties) {
        node.addMixin('nls:orthophoto');
        outputProperties.put(MetadataProperty.GMD_DISTRIBUTIONFORMAT, "JPEG2000");
        var orthoid = node.path.substring(10);
        orthoid = orthoid.substring(0, orthoid.lastIndexOf('.'));
        outputProperties.put(MetadataProperty.NLS_ORTHOPHOTOID, orthoid);
    }
});