function toggleMetaVisibility(iD)
{
  var div = document.getElementById(iD); 

  var visibility = div.style;
  
  if(visibility.display=='' || visibility.display=='none') {

	  visibility.display = 'table-row';
	
  } else {
	  
	  visibility.display = 'none';
	  
  }
  
} 