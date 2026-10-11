# Setup RClone

1. Setup rclone utility

   ```bash
   curl https://rclone.org/install.sh | sudo bash

   ```

   or via package manager

2. Run rclone configurator

    ```bash
    rclone config

    ```

3. Specify answers:

   - New connection: n (no)
   - Name: for example: **yadisk**
   - Storage type: search for Yandex by name or number
   - client_id / client_secret: leave blank, no need
   - Run extended config: n (no)
   - Use browser for auth: n (no) - for scripts and server/automated connections
   - Use saved token (or generate it once more - opening the URL you will get after the previous answer)

4. Check connection:

    ```bash
    rclone lsd yadisk:
    ```
