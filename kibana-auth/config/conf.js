// config/conf.js

// expose our config directly to our application using module.exports
module.exports = {

    'googleAuth' : {
        'clientID'         : 'id',
        'clientSecret'     : 'secret',
        'callbackURL'      : 'http://localhost:8080/auth/google/callback'
    },
    'kibana' : {
    	'rootURL'			: 'http://localhost:5601/',
        'dashboardURL'		: 'http://localhost:5601/#/dashboard?_g=()',
        'discoverURL'		: 'http://localhost:5601/#/discover?_g=()',
        'settingsURL'		: 'http://localhost:5601/#/settings/indices/?_g=()',
        'visualizeURL'		: 'http://localhost:5601/#/visualize/step/1?_g=()'
    }
};
