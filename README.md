brooklyn-ravello
================
Linking Brooklyn and Ravello.

Required properties, to be given in brooklyn.properties:

```
brooklyn.location.named.ravello=rr
brooklyn.location.named.ravello.username
brooklyn.location.named.ravello.password
brooklyn.location.named.ravello.privateKeyFile
brooklyn.location.named.ravello.privateKeyId
```

The privateKeyId value should be the ID given by Ravello to the key contained in privateKeyFile. Find this by making a GET to https://cloud.ravellosystems.com/services/keypairs.

Optional properties:
```
brooklyn.location.named.ravello.preferredCloud=HPCLOUD
brooklyn.location.named.ravello.preferredRegion=US West AZ 1
```

Check `io.cloudsoft.ravello.dto.Cloud` for known clouds and regions. If the properties are given they are assumed to be correct (so you can deploy to future clouds if you know their names). If the properties are missing we use Amazon/Virginia.

================

&copy; Cloudsoft Corporation 2013. All rights reserved.

Use of this software is subject to the Cloudsoft EULA, provided in licence.txt and at http://www.cloudsoftcorp.com/cloudsoft-developer-license