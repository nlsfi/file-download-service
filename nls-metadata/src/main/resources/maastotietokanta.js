/**
 * Maastotietokanta
 * Metadatan k�sittely 
 **/

importClass(Packages.fi.nls.fileservice.jcr.MetadataProperty);

handlerRegistry.registerDatasetHandler((function () {

	var fileIdentifier = 'cfe54093-aa87-46e2-bfa2-a20def7b036f';
	var datasetName = 'maastotietokanta';
	//var datasetVersion = 'kaikki';
	var pattern = /\/tuotteet\/maastotietokanta\/(\S*)\/(\S*)\/(\S*)\/\S*\/\S+\/(\S*?)(?:|.mif|.shp|_mtk).zip/i; 
	
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
				//outputProperties.put(MetadataProperty.NLS_DATASETVERSION,datasetVersion);
			}
	};
	
	return handler;
		
}()));