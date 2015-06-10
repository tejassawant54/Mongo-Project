<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>MONGODB SEARCH</title>
<script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
<script type="text/javascript">
	function dropDownChange(val){	//Function for printing the value
		//alert("aa");
		//console.log(val);
		if(val.length == " ")
		{
		alert("No Content to Display!!");
		}
		else
		{
		$("#dropDownValue").html($.trim(val));
		}
	}
</script>
<style type="text/css">
body{
background-color: #ccc;
}
</style>
</head>
<body>
<h1>MONGO SEARCH</h1>
<form action="Blog.php" method="POST"> 			
<input type="text" name="stext" value=""/>
<input type="submit" name="SEARCH" value="SEARCH"/>
</form>
<?php
error_reporting(0);
// connect
//echo "<h1>Mongo PHP Connection</h1>";
echo extension_loaded("mongo")?" ":"MongoDB Driver NOTloaded\n";
$m = new Mongo("mongodb://localhost"); //parameters can be nothing; can also add authentication here

// select a database
$db = $m->SampleSocial;

// select a collection (analogous to a relational database's table)
$collection = $db->Blog;

$Searchitem = $_POST['stext'];

echo "<p>YOUR SEARCH CRITERIA IS:   ".$Searchitem."</p>";

//$cursor = $collection->find(array('title'=>array('$regex'=>".*".$Searchitem.".*")));
$cursor = $collection->find(array('title' => new MongoRegex("/^(.*?(".$Searchitem.")[^$]*)$/i")));

echo "<p>COUNT: ".$cursor->count()."</p>";

echo "<select name = 'user' onchange='dropDownChange(this.value);'>";
echo "<option value="."'". $document["content:encoded"]."'".">SELECT TITLE</option>";
foreach ($cursor as $document){ 
	//echo "<p>".$document["title"]." ".$document["content:encoded"]."</p>";
	echo "<option value="."'".$document["content:encoded"]."'".">".$document["title"]. "</option>";
	
}
echo "</select>"; 

?>

<p>THE REQUIRED TEXT IS:</p>
<div id='dropDownValue'></div>

</body>
</html>