/**
 * Korkeusmalli 10m
 * Metadatan k�sittely 
 **/

importClass(Packages.fi.nls.fileservice.jcr.MetadataProperty);

handlerRegistry.registerDatasetHandler((function () {

	var fileIdentifier = 'cd640425-315f-4b12-86c5-192e98701dcb';
	var datasetName = 'korkeusmalli_10m';
	var pattern = /\/tuotteet\/korkeusmalli_10m\/(\S*)\/(\S*)\/(\S*)\/[A-Z][0-9]\/[A-Z][0-9][0-9]\/(\S*).(zip|png)/i;
	
	var mappers = new Array();
	mappers.push(defaultMapper(MetadataProperty.NLS_DATASETVERSION));
	mappers.push(lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings));
	mappers.push(lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings));
	mappers.push(defaultMapper(MetadataProperty.NLS_GRIDCELL));
	mappers.push(noopMapper());
	
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
				
				 if (outputProperties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT).equals("PNG")) {
                     var name = node.getName();
                     name = name.substring(0,name.lastIndexOf("."));
                     var arr = java.lang.reflect.Array.newInstance(java.lang.String, 1);
                     arr[0] = name + ".pgw";

                     outputProperties.put(MetadataProperty.NLS_RELATED, arr);
				 }

			}
	};
	
	return handler;
		
}()));