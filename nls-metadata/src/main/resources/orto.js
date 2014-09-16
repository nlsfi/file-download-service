/**
 * Maanmittauslaitoksen ortokuva
 * Metadatan k�sittely 
 **/

importClass(Packages.fi.nls.fileservice.jcr.MetadataProperty);

handlerRegistry.registerDatasetHandler((function () {
	var fileIdentifier = 'b20a360b-1734-41e5-a5b8-0e90dd9f2af3';
	var datasetName = 'orto';
	var pattern = /\/tuotteet\/orto\/(\S*)\/(\S*)\/(\S*)\/[A-Z][0-9]{2}\/(\S{3})\/([0-9]+)\/(\S*:?)\.jp2/i;
	
	
	var orthoVersionMappings = {};
	orthoVersionMappings['mara_mv_20000_50'] = 'ortokuva';
	orthoVersionMappings['mara_mv_35000_100'] = 'ortokuva';
	orthoVersionMappings['mara_v_20000_50'] = 'ortokuva';
	orthoVersionMappings['mara_v_25000_50'] = 'ortokuva';
	orthoVersionMappings['mavi_v_25000_50'] = 'ortokuva';
	orthoVersionMappings['mara_vv_20000_50'] = 'vaaravari_ortokuva';
	orthoVersionMappings['mara_vv_25000_50'] = 'vaaravari_ortokuva';
	orthoVersionMappings['mavi_vv_25000_50'] = 'vaaravari_ortokuva';
		
	var mappers = new Array();
	mappers.push(lookupTableMapper(MetadataProperty.NLS_CRS,crsMappings));
	mappers.push(lookupTableMapper(MetadataProperty.NLS_DATASETVERSION, orthoVersionMappings));
	mappers.push(defaultMapper(MetadataProperty.NLS_YEAROFPHOTOGRAPHY));
	mappers.push(defaultMapper(MetadataProperty.NLS_ELEVATIONMODEL));
	mappers.push(defaultMapper(MetadataProperty.NLS_FILEVERSION));
	mappers.push(defaultMapper(MetadataProperty.NLS_GRIDCELL));
	//mappers.push(lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings));
	//mappers.push(lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings));
	
	var handler = {
		supportsPath: function (path) {
			return path.match(pattern);
		},
		fileIdentifier : fileIdentifier,
		processNode: function (node, outputProperties) {
			node.addMixin('nls:datasetfile');
			node.addMixin('nls:orthophoto');
			node.addMixin('gmd:metadata');
			
			getPropertiesFromPath(pattern,node.path,mappers,outputProperties);
			outputProperties.put(MetadataProperty.GMD_FILEIDENTIFIER, fileIdentifier);
			outputProperties.put(MetadataProperty.NLS_DATASET,datasetName);
			outputProperties.put(MetadataProperty.GMD_DISTRIBUTIONFORMAT, 'JPEG2000');
			
			var orthoid = node.path.substring(10);
			orthoid = orthoid.substring(0, orthoid.lastIndexOf('.'));
			outputProperties.put(MetadataProperty.NLS_ORTHOPHOTOID, orthoid);
			//outputProperties.put(MetadataProperty.NLS_RELATED, outputProperties.get(MetadataProperty.NLS_GRIDID) + '.tfw');
		}
	};
	return handler;
	
}()));
