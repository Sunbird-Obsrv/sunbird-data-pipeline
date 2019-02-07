Role Name
=========

Druid role to install and manage druid services

Requirements
------------

The following pre-requisites should be installed for this role

java

zookeeper - for management of current cluster state

postgres - for meta storage

Role Variables
--------------
defaults/main.yml

Deploy directory for the druid package is mentioned in druid_path

Common Variables: Contains the variables that are common to all druid services like zookeeper host, postgres host , log directories

azure variables (used for deep storage) will be taken from groupvars if not specified in groups vars , the role will fail

druid extenstions load list  can added to druid_extensions_list variable with comma seperated 

druid third party extensions can be added to druid_community_extensions variable as array 

Specific to each service(coordinator, overlord etc) variables has  beeen seperated and declared 

Dependencies
------------

zookeeper-upgrade

postgres-provision

Example Playbook
----------------

Including an example of how to use your role (for instance, with variables passed in as parameters) is always nice for users too:

    - hosts: servers
      roles:
        - {role: analytics-druid,when: "'coordinator' in group_names",druid_role: 'coordinator', service: 'coordinator' }
        - {role: analytics-druid,when: "'overlord' in group_names",druid_role: 'overlord', service: 'overlord'}
