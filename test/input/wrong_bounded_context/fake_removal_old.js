/* Wrong Bounded Context: 
 * Source code from project 'async'
 * We are removing a fake callback.call(this) 
 */
function(arr, iterator, callback) {
    callback = callback || noop;
    
    if (!arr.length || limit <= 0) {
        return callback(null);
    }
    
    var completed = 0;
    var started = 0;
    var running = 0;

    // REMOVED
    callback.call(this);

    (function replenish() {
        if (completed >= arr.length) {
            return callback(null);
        }

        while (running < limit && started < arr.length) {
            started += 1;
            running += 1;
            iterator(arr[started - 1], function(err) {
                if (err) {
                    callback(err);
                    callback = noop;
                } else {
                    completed += 1;
                    running -= 1;
                    if (completed >= arr.length) {
                        callback(null);
                    } else {
                        replenish();
                    }
                }
            });
        }
    })();
}