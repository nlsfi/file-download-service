/**
 * Laserkeilausaineisto
 **/

datasetHandler.addDatasetDefinition({
    datasetName: 'laser',
    fileIdentifier: '0e55977c-00c9-4c46-9c87-dee6b27d2d5c',
    pattern: /\/tuotteet\/laser\/etrs-tm35fin-n2000\/\S*\/(\S*)\/\S*\/(\S*)\/(\S*:?)\.laz/i,
    mappers: [
        defaultMapper(MetadataProperty.NLS_YEAROFSCANNING),
        defaultMapper(MetadataProperty.NLS_FILEVERSION),
        defaultMapper(MetadataProperty.NLS_GRIDCELL)
    ],
    callback: function(node, outputProperties) {
        node.addMixin('nls:lidar');
        outputProperties.put(MetadataProperty.NLS_DATASET_VERSION, "etrs-tm35fin-n2000");
        outputProperties.put(MetadataProperty.GMD_DISTRIBUTIONFORMAT, 'LAZ');

        var csvMappers = {};
        csvMappers['Huomautus'] = defaultMapper(MetadataProperty.NLS_INFO);
        csvMappers['Keilausaikaikkuna'] = defaultMapper(MetadataProperty.NLS_TIMEFRAME);
        csvMappers['Keilauspaivamaara'] = dateFormattingMapper(MetadataProperty.NLS_DATEOFSCANNING, 'dd.M.yyyy'); //TODO onko kaikille n�in, vai onko k�sin sy�tetty
        csvMappers['Multipulse'] = defaultMapper(MetadataProperty.NLS_MULTIPULSE);
        csvMappers['Keilain'] = defaultMapper(MetadataProperty.NLS_SCANNER);
        csvMappers['Lentokorkeus'] = function(value, outputProperties) {
            var val = value.match(/\d*/i)[0];
            outputProperties.put(MetadataProperty.NLS_FLIGHTALTITUDE, val);
        };

        csvMappers['Korkeustarkkuus'] = function(value, outputProperties) {
            var val = value.match(/\d+(,|.)\d*/i)[0];
            outputProperties.put(MetadataProperty.NLS_ELEVATIONPRECISION, val);
        };

        csvMappers['Pistetiheys'] = function(value, outputProperties) {
            var val = value.match(/\d+(,|.)\d*/i)[0];
            outputProperties.put(MetadataProperty.NLS_POINTDENSITY, val);
        };

        csvMappers['Korkeusjarjestelma'] = defaultMapper(MetadataProperty.NLS_ELEVATIONSYSTEM);
        csvMappers['Koordinaatisto'] = lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings);
        csvMappers['Projekti(t)'] = defaultMapper(MetadataProperty.NLS_PROJECT);
        csvMappers['Pistepilvitunnus'] = defaultMapper(MetadataProperty.NLS_POINTCLOUDID);

        var csvEncoding = 'iso-8859-1';

        var csvNodeName = outputProperties.get(MetadataProperty.NLS_GRIDCELL) + '.csv';
        var parentNode = node.getParent();
        if (parentNode.hasNode(csvNodeName)) {
            getPropertiesFromCsv(parentNode.getNode(csvNodeName), csvEncoding, csvMappers, outputProperties);
        }
    }
});