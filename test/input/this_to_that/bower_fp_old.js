Manager.prototype._dissect = function () {
    var err;
    var promises = [];
    var suitables = {};

    // If something failed, reject the whole resolve promise
    // with the first error
    if (this._hasFailed) {
        clearTimeout(this._failFastTimeout); // Cancel fail fast timeout

        err = mout.object.values(this._failed)[0][0];
        this._deferred.reject(err);
        return;
    }

    mout.object.forOwn(this._resolved, function (decEndpoints, name) {
        var promise;
        var semvers;
        var nonSemvers;

        // Filter semver ones
        semvers = decEndpoints.filter(function (decEndpoint) {
            return !!decEndpoint.pkgMeta.version;
        });

        // Sort semver ones
        semvers.sort(function (first, second) {
            if (semver.gt(first, second)) {
                return -1;
            }
            if (semver.lt(first, second)) {
                return 1;
            }
            return 0;
        });

        // Filter non-semver ones
        nonSemvers = decEndpoints.filter(function (decEndpoint) {
            return !decEndpoint.pkgMeta.version;
        });

        // HERE IS THE CHANGE
        promise = this._electSuitable(name, semvers, nonSemvers)
        .then(function (suitable) {
            suitables[name] = suitable;
        });

        promises.push(promise);
    }, this);

    return Q.all(promises)
    .then(function () {
        // Filter only packages that need to be installed
        this._dissected = mout.object.filter(suitables, function (decEndpoint, name) {
            var installedMeta = this._installed[name];
            return !installedMeta || installedMeta._release !== decEndpoint.pkgMeta._release;
        }, this);

        // Resolve with the package metas of the dissected object
        return mout.object.map(this._dissected, function (decEndpoint) {
            return decEndpoint.pkgMeta;
        });
    }.bind(this))
    .then(this._deferred.resolve, this._deferred.reject);
};