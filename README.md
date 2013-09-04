brooklyn-ravello
================
Linking Brooklyn and Ravello.

Required properties:

```
ravello.username
ravello.password
ravello.privateKeyFile
ravello.privateKeyId
```

The privateKeyId value should be the ID given by Ravello to the key contained in privateKeyFile. Find this by making a GET to https://cloud.ravellosystems.com/services/keypairs.

Optional properties:
```
brooklyn.location.named.ravello.preferredCloud=HPCLOUD
brooklyn.location.named.ravello.preferredRegion=US West AZ 1
```

Check `io.cloudsoft.ravello.dto.Cloud` for known clouds and regions. If the properties are given they are assumed to be correct (so you can deploy to future clouds if you know their names). If the properties are missing we use Amazon/Virginia.

Give properties in brooklyn.properties in format `brooklyn.location.named.ravello.<prop> = <value>`
