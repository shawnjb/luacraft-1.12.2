mc.broadcast("Starting test run script.")

mc.broadcast("Executing /say command...")
mc.execute("/say Hello, Minecraft world! This is a test from Lua!")

mc.broadcast("Executing /time set command...")
mc.execute("/time set 1000")

mc.broadcast("Executing /tp command...")
mc.execute("/tp @p 100 64 100")

mc.broadcast("Executing /give command...")
mc.execute("/give @p diamond_sword 1")

mc.broadcast("Executing /summon command...")
mc.execute("/summon zombie 100 64 100")

mc.broadcast("Executing /weather clear command...")
mc.execute("/weather clear")

mc.broadcast("Executing /difficulty hard command...")
mc.execute("/difficulty hard")

mc.broadcast("Executing /list command to show online players...")
mc.execute("/list")

mc.broadcast("Executing /spawnpoint command...")
mc.execute("/spawnpoint @p 200 64 200")

mc.broadcast("Test run script finished.")
