'use strict';

var SlingSettingsService = Packages.org.apache.sling.settings.SlingSettingsService;

use([], function() {
    const slingSettingsService = sling.getService(SlingSettingsService);

    function Runmodes() {
        this.runmodes = [];
        var runmodesSet = slingSettingsService.getRunModes();
        var iterator = runmodesSet.iterator();

        while (iterator && iterator.hasNext()) {
            var item = iterator.next();
            this.runmodes.push('' + item);
        }
    }

    Runmodes.prototype.hasRunmode = function(name) {
        return this.runmodes.indexOf(name) > -1;
    };

    Runmodes.prototype.getRunmodes = function() {
        return this.runmodes;
    };

    return new Runmodes();
});
