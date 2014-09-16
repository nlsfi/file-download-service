/**
 * Laserkeilausaineisto
 * Metadatan k�sittely 
 **/

importClass(Packages.fi.nls.fileservice.jcr.MetadataProperty);

handlerRegistry.registerDatasetHandler((function () {

	var fileIdentifier = '0e55977c-00c9-4c46-9c87-dee6b27d2d5c';
	var datasetName = 'laser';
	//var pattern = /\/tuotteet\/laser\/\S*\/\S*\/(\S*)\/\S*\/(\S*)\/(\S*:?)\.(laz)/i; // todo versio??
	var pattern = /\/tuotteet\/laser\/etrs-tm35fin-n2000\/\S*\/(\S*)\/\S*\/(\S*)\/(\S*:?)\.laz/i; // todo versio??
	
	
	var mappers = new Array();
	mappers.push(defaultMapper(MetadataProperty.NLS_YEAROFSCANNING));
	mappers.push(defaultMapper(MetadataProperty.NLS_FILEVERSION));
	mappers.push(defaultMapper(MetadataProperty.NLS_GRIDCELL));
	mappers.push(lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings));
	
	var csvEncoding = 'iso-8859-1';

	/*
	Pistepilvitunnus;laser/automaattinen/2008/20080001_MML_Loimaa/1/L3432B4
	Projekti(t);20080001_MML_Loimaa
	Koordinaatisto;TM35FIN
	Korkeusjarjestelma;N2000
	Pistetiheys;0,74 pistetta/m2
	Korkeustarkkuus;0,15 m
	Lentokorkeus;1800 m
	Keilain;Leica ALS50-II
	Multipulse;Ei
	Keilauspaivamaara;4.4.2008
	Keilausaikaikkuna;A/B
	Huomautus;*/

	
	var csvMappers = {};
	csvMappers['Huomautus'] = defaultMapper(MetadataProperty.NLS_INFO);
	csvMappers['Keilausaikaikkuna'] = defaultMapper(MetadataProperty.NLS_TIMEFRAME);
	csvMappers['Keilauspaivamaara'] = dateFormattingMapper(MetadataProperty.NLS_DATEOFSCANNING, 'dd.M.yyyy'); //TODO onko kaikille n�in, vai onko k�sin sy�tetty
	csvMappers['Multipulse'] = defaultMapper(MetadataProperty.NLS_MULTIPULSE);
	csvMappers['Keilain'] = defaultMapper(MetadataProperty.NLS_SCANNER);
	csvMappers['Lentokorkeus'] = function(value,outputProperties) {
		var val = value.match(/\d*/i)[0];
		outputProperties.put(MetadataProperty.NLS_FLIGHTALTITUDE, val);
	}; 
		
	csvMappers['Korkeustarkkuus'] = function(value,outputProperties) {
		var val = value.match(/\d+(,|.)\d*/i)[0];
		outputProperties.put(MetadataProperty.NLS_ELEVATIONPRECISION, val);
	}; 
			
	csvMappers['Pistetiheys'] = function(value,outputProperties) {
		var val = value.match(/\d+(,|.)\d*/i)[0];
		outputProperties.put(MetadataProperty.NLS_POINTDENSITY, val);
	};
	
	csvMappers['Korkeusjarjestelma'] = defaultMapper(MetadataProperty.NLS_ELEVATIONSYSTEM);
	csvMappers['Koordinaatisto'] = lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings);
	csvMappers['Projekti(t)'] = defaultMapper(MetadataProperty.NLS_PROJECT);
	csvMappers['Pistepilvitunnus'] = defaultMapper(MetadataProperty.NLS_POINTCLOUDID);
	
	var handler = {
			supportsPath: function (path) {
				return path.match(pattern);
			},
			fileIdentifier : fileIdentifier,
			processNode: function (node, outputProperties) {
				node.addMixin('nls:datasetfile');
				node.addMixin('nls:lidar');
				node.addMixin('gmd:metadata');
				
				getPropertiesFromPath(pattern,node.path,mappers,outputProperties);
				getPropertiesFromCsv(node.getParent().getNode(outputProperties.get(MetadataProperty.NLS_GRIDCELL) + '.csv'),
						csvEncoding, csvMappers, outputProperties);
				outputProperties.put(MetadataProperty.GMD_FILEIDENTIFIER,fileIdentifier);
				outputProperties.put(MetadataProperty.NLS_DATASET,datasetName);
				outputProperties.put(MetadataProperty.NLS_DATASETVERSION, 'etrs-tm35fin-n2000');
				outputProperties.put(MetadataProperty.GMD_DISTRIBUTIONFORMAT, 'LAZ');
			}
	};
	
	return handler;
		
}()));