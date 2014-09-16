/**
 * Karttalehtijako
 * Metadatan käsittely 
 **/

importClass(Packages.fi.nls.fileservice.jcr.MetadataProperty);

handlerRegistry.registerDatasetHandler((function () {

	//var fileIdentifier = '5c2ba253-e1b0-42c8-b9bb-3bac947e1cf1';
	var fileIdentifier = '';
	var datasetName = 'karttalehtijako_ruudukko';
	var pattern = /\/tuotteet\/karttalehtijako_ruudukko\/(\w*)\/(\w*)\/(\w*)\/(?:\w*).zip$/i;
	
	var mappers = new Array();
	mappers.push(defaultMapper(MetadataProperty.NLS_DATASETVERSION));
	mappers.push(lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings));
	mappers.push(lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings));
		
	var handler = {
			supportsPath: function (path) {
				return path.match(pattern);
			},
			fileIdentifier : fileIdentifier,
			processNode: function (node, outputProperties) {
				node.addMixin('nls:datasetfile');
				node.addMixin('gmd:metadata');
				getPropertiesFromPath(pattern,node.path,mappers,outputProperties);
				outputProperties.put(MetadataProperty.NLS_DATASET,datasetName);
			}
	};
	
	return handler;
		
}()));