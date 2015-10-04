/* Not Defined: Assigned elsewhere.
 * Output: None */
var pm2_env = JSON.parse(process.env.pm2_env);

for(var k in pm2_env) {
    process.env[k] = pm2_env[k];
}

delete process.env.pm2_env;

var pmId = pm2_env.pm_id;
