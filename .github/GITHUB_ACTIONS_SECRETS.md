# Secretos GitHub Actions (8 + Oracle)

## Por que fallo SSH (`Permission denied publickey`)

El build y push a Docker Hub **ya funcionaron**. El fallo fue solo al conectar por SSH a EC2.

Causas tipicas:

1. **EC2_SSH_KEY** mal pegada (sin saltos de linea, cortada, o no es el .pem del par de claves de la instancia).
2. **USER_SERVER** incorrecto (Amazon Linux = `ec2-user`).
3. **EC2_HOST** no es la IP elastica actual de la instancia.
4. El workflow antiguo usaba `echo` para guardar el .pem y a veces **rompe** el formato de la clave.

## Secretos obligatorios

| Secret | Valor |
|--------|--------|
| `DOCKERHUB_USERNAME` | Usuario Docker Hub |
| `DOCKERHUB_TOKEN` | Token `dckr_pat_...` |
| `AWS_ACCESS_KEY_ID` | AWS Academy (Details del lab) |
| `AWS_SECRET_ACCESS_KEY` | AWS Academy |
| `AWS_SESSION_TOKEN` | AWS Academy (obligatorio en labs temporales) |
| `EC2_HOST` | IP elastica, ej. `34.205.57.70` |
| `EC2_SSH_KEY` | Contenido **completo** del archivo `.pem` |
| `USER_SERVER` | `ec2-user` (Amazon Linux) |
| `ORACLE_USERNAME` | `INSCRIPCION_APP` |
| `ORACLE_PASSWORD` | Password del usuario Oracle |

## Como pegar EC2_SSH_KEY correctamente

1. Abre el `.pem` con **Bloc de notas** o VS Code (no Word).
2. Debe verse asi (varias lineas):

```
-----BEGIN RSA PRIVATE KEY-----
MIIEowIBAAKCAQEA...
...
-----END RSA PRIVATE KEY-----
```

3. Copia **todo** desde `-----BEGIN` hasta `-----END` inclusive.
4. GitHub → Settings → Secrets → `EC2_SSH_KEY` → **Update** → pega y guarda.

## Wallet Oracle en EC2 (una vez, manual)

El wallet no esta en GitHub. En la terminal de EC2:

```bash
mkdir -p /home/ec2-user/wallet
```

Sube los archivos de `Wallet_miQuintaBD` a `/home/ec2-user/wallet` (SCP, SFTP o copiar desde consola AWS).

## Probar SSH desde tu PC

```powershell
ssh -i "ruta\a\tu-clave.pem" ec2-user@TU_IP_ELASTICA
```

Si aqui falla, GitHub Actions tambien fallara hasta corregir clave o IP.

## Security Group

Debe permitir **SSH puerto 22** desde `0.0.0.0/0` (o al menos desde internet para el runner de GitHub) y **8080** para Postman.
