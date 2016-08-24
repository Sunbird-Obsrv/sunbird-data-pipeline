// config/conf.js

// expose our config directly to our application using module.exports
module.exports = {

    'googleAuth' : {
        'clientID'         : '366420418948-ov5sn3pls3p3djb2t15r42f7d4j0ps1l.apps.googleusercontent.com',
        'clientSecret'     : 'LMKXuScl1dwatNN1xgsd2RDa',
        'callbackURL'      : 'http://localhost:8080/auth/google/callback'
    },
    'kibana' : {
        'prefix'            : '/kibana',
        'port'              : '3000',
    	'rootURL'			: 'http://localhost:5601/',
        'dashboardURL'		: 'http://localhost:5601/#/dashboard?_g=()',
        'discoverURL'		: 'http://localhost:5601/#/discover?_g=()',
        'settingsURL'		: 'http://localhost:5601/#/settings/indices/?_g=()',
        'visualizeURL'		: 'http://localhost:5601/#/visualize/step/1?_g=()'
    }
};
