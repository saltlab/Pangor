/* Callback Missing: Callback is missing on one path. 
 * Output: CB_MISSING_CALLBACK (callback) */

getAlbums = function(callback){
  setTimeout(function(){
        db.query('SELECT * FROM albums ORDER BY album asc', {
            album   : String,
            artist  : String,
            year    : Number,
            genre   : String,
            cover   : String
        },  
        function(err, rows) {
            if(err) console.log('Database error: ' + err);

            if (rows !== undefined && rows !== null ){
                console.log(rows);
                if(rows.length > 0){ 
                    console.log('Found albums...');
                    callback(rows);
                }   
            } else {
                callback(null);
            }   
        }); 
  },300);
}
