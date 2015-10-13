res.links = function(links){
  var linkField = typeof this.get('Link') === 'undefined' ?
    '' :  this.get('Link') + ', ';
  return this.set('Link', linkField + Object.keys(links).map(function(rel){
    return '<' + links[rel] + '>; rel="' + rel + '"';
  }).join(', '));
};