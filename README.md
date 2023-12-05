# iac-pulumi

# command uploading ssl certificate using aws cli
aws acm import-certificate --certificate fileb://certificate.crt --private-key fileb://private.key --certificate-chain fileb://ca_bundle.crt
