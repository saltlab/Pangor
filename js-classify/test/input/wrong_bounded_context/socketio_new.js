function(env, fn) {
  if ('function' == typeof env) {
    env.call(this);
  } else if (env == process.env.NODE_ENV) {
    fn.call(this);
  }
  return this;
}