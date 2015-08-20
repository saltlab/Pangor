function(env, fn) {
  if ('function' == typeof env) {
    env();
  } else if (env == process.env.NODE_ENV) {
    fn();
  }
  return this;
}