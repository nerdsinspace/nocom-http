# Deployment

The only service deployed for nhack is a Jenkins instance, accessible at:

https://jenkins.nhackindustries.com

Tristan is hosting this on a VM. Besides what's codified in this repository, Tristan added:

* backups with https://github.com/starcraft66/borgbackup-ansible
* other postinstall configuration, like a `linuxauto` user

## Jenkins 

For new installations, ensure that `inventory/jenkins` is pointed to a fresh install of Ubuntu 18.04:

```bash
[jenkins]
jenkins_master ansible_host=jenkins.nhackindustries.com ansible_user=ubuntu
```

Then, execute the `nhack-jenkins.yml` playbook:

```bash
$ ansible-playbook -vv  -i deployment/ansible/inventory deployment/ansible/nhack-jenkins.yml
```

You can also execute the playbook to push configuration updates to the existing Jenkins instance.
