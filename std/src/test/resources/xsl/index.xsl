<?xml version="1.0" encoding="UTF-8"  ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
  <xsl:output method="xml" indent="no" encoding="UTF-8" doctype-public="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />

  <xsl:template match="/root">
    <html xmlns="http://www.w3.org/1999/xhtml" lang="ru">
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0" />
        <meta name="apple-mobile-web-app-capable" content="yes" />
        <meta name="apple-mobile-web-app-status-bar-style" content="black" />
        <title>Invest</title>
        <script type="text/javascript" language="javascript" charset="utf-8" src="/assets/jquery-3.1.1.min.js">//</script>
        <script type="text/javascript" language="javascript" charset="utf-8" src="/assets/chart.js">//</script>
        <link rel="icon" type="image/x-icon">
          <xsl:attribute name="href"><![CDATA[data:image/x-icon;base64,AAABAAEAEBAAAAEAIABoBAAAFgAAACgAAAAQAAAAIAAAAAEAIAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAABYtOYQXbnrcF+56b9SnsrfRoOq/zlnit8yWXq/M2WLcDVwnBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVbDi71677P9+z/H/W77x/y2W1P8PYpP/FWGO/yxum/81dqXvQ57bvzF0q88rXYn/I0lwzyxSdb8zZYtwNXCcEFWw4v900/7/wvH8/836//+99v//t/T//5HP4/9Epdb/Nnam/0eh2/8vg8T/Flye/whBcv8SVoX/LG6b/zV2pe9TreD/k+T7/4vb9v90ye//Za/U/0aDqv9Xiqb/TX2b/zVvm/9qtuD/aqzQ/1uXvv9lo8f/drbT/0Sl1v82dqb/SKLVv1Sv4f9eu+z/fs/x/1u+8f8tltT/D2KT/xVhjv8sbpv/OHqp/zd8sf8iUX//MFyC/0Nykf9qr8z/OX6u/1i05hBYtOb/Xr3u/2Gt1f9YkLX/U4Sm/0l5m/9TjbH/PJ3Q/zZ2pv8vg8T/Flye/whBcv8TXov/LG6b/zd5qP9VsOLvXrvs/37P8f9bvvH/LZbU/w9ik/8VYY7/LG6b/zh7q/85fq7/aqzQ/1uXvv9pp8r/kc/j/0Sl1v82dqb/VbDi/3TT/v/C8fz/zfr//732//+39P//kc/j/0Sl1v82dqb/Q5zT/zd8sf8vYo7/Pm2Q/1qKo/9qr8z/OX6u/1Ou4f+Y6Pz/p/P//6fz//+n8///p/P//6fz//+i7v//OX6u/0eh2/8vg8T/Flye/wxXiP8VYY7/LG6b/zl+rf9IotW/VbDd/2/M7v983vv/fOH//4Hm//+O3PX/bLvl/0+r3v9tvOX/aqzQ/1uXvv+z7/v/kc/j/0Sl1v82dqb/AAAAAEii1TBIotWASKLVgEii1YBIotWASqXYijea3/9NtfH/RJ/a/zyDtv9Lh63/V4qm/1qKo/9qr8z/OX6u/wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABFpOD/Tq/q/3TF7P9bvvH/LZbU/w9ik/8VYY7/LG6b/zl+rf8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVbDi/3TT/v/C8fz/zfr//732//+39P//kc/j/0Sl1v82dqb/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFOu4f+Y6Pz/p/P//6fz//+n8///p/P//6fz//+i7v//OX6u/wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABIotW/VbDd/2/M7v983vv/fOH//4Hm//+O3PX/bLvl/0ii1b8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEii1TBIotWASKLVgEii1YBIotWASKLVgEii1TAAAAAAwf8AAAADAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAD+AAAA/gAAAP4AAAD+AAAA/4MAAA==]]></xsl:attribute>
        </link>
        <link rel="stylesheet" href="/assets/font-awesome/css/font-awesome.css" />
        <script type="text/javascript" language="javascript" charset="utf-8">
          <xsl:attribute name="src">/assets/invest.js?<xsl:value-of select="startTime" /></xsl:attribute>
          <xsl:text>//</xsl:text>
        </script>
      </head>
      <body>
        <noscript>Your browser does not support JavaScript!</noscript>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
