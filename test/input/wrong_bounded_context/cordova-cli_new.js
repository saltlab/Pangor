function(cfg) {
    this.update_www();
    
    return this.update_from_config(cfg).then(function() {
        this.update_overrides();
        this.update_staging();
        util.deleteSvnFolders(this.www_dir());
    }.bind(this));
}