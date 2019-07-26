# Docker run options

-   Add Environments
    -   SECRET: xxx
-   Mount port
    -   8000:8000
-   Run example args
    -   docker run --link mysql:mysql --link redis:redis -p 8000:8000 -e SECRET=test -d registry.cn-hangzhou.aliyuncs.com/ttzztztz/6rabbit-backend:latest