/**
 * Maastokarttarasteri 1: 50 000
 * Metadatan k�sittely 
 **/

importClass(Packages.fi.nls.fileservice.jcr.MetadataProperty);

handlerRegistry.registerDatasetHandler((function () {

	var fileIdentifier = 'd47ac165-6abd-4357-a4f9-a6f17e2b0c58';
	var datasetName = 'maastokarttarasteri';
	var pattern = /\/tuotteet\/maastokarttarasteri\/(\S*)\/\S*\/(\S*)\/(\S*)\/\S*\/\S*\/U([a-zA-Z][0-9]*)_\S*.tif/i; 
				   
	var mappers = new Array();
	mappers.push(defaultMapper(MetadataProperty.NLS_DATASETVERSION));
	mappers.push(lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings));
	mappers.push(lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings));
	mappers.push(defaultMapper(MetadataProperty.NLS_GRIDCELL));

	var handler = {
			supportsPath: function (path) {
				return path.match(pattern);
			},
			fileIdentifier : fileIdentifier,
			processNode: function (node, outputProperties) {
				node.addMixin('nls:datasetfile');
				node.addMixin('gmd:metadata');
				getPropertiesFromPath(pattern,node.path,mappers,outputProperties);
				outputProperties.put(MetadataProperty.GMD_FILEIDENTIFIER,fileIdentifier);
				outputProperties.put(MetadataProperty.NLS_DATASET,datasetName);
				
				var name = node.getName();
				name = name.substring(0,name.lastIndexOf("."));
				
				var arr = java.lang.reflect.Array.newInstance(java.lang.String, 2);
				arr[0] = name + ".tab";
				arr[1] = name + ".tfw";
				
				outputProperties.put(MetadataProperty.NLS_RELATED, arr);
				
			}
	};
	
	return handler;
		
}()));