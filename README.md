# Player music javazoom interface

Player de música feito em Java, baseado na lib player javazoom.jl.player.advanced.AdvancedPlayer

Referências

* [JavaZoom](http://www.javazoom.net/index.shtml)
* [JavaZoom JavaLayer](http://www.javazoom.net/javalayer/javalayer.html)
* [Mp3 player](https://introcs.cs.princeton.edu/java/faq/mp3/MP3.java.html)
* [stackoverflow play/pause](https://stackoverflow.com/questions/16882354/how-to-play-pause-a-mp3-file-using-the-javazoom-jlayer-library)

FlatLaf

* [FlatLaf](https://mvnrepository.com/artifact/com.formdev/flatlaf/0.38)
* [FlatLaf download](https://www.formdev.com/flatlaf/#download)
* [Themes](https://www.formdev.com/flatlaf/themes/)


![Player v1.0.0](https://raw.githubusercontent.com/surfx/playermusicjavazoominterface/master/imagens/player_img.png)


## Shell

Exportar o runnable jar (player1.0.0.jar)

```
#!/bin/bash

cd /home/.../Documents/programas/softwares/player
/usr/lib/jvm/openjdk-8u332/bin/java -jar player1.0.0.jar &
```

## player.desktop

```
cd /usr/share/applications
sudo gedit player.desktop
```

```
[Desktop Entry]
Type=Application
Name=Player
Comment=Player
Comment[pt_BR]=Player
Encoding=UTF-8
Exec=nohup /bin/bash /home/.../Documents/programas/softwares/player/player.sh
GenericName=SqlDeveloper
GenericName[pt_BR]=Player
Icon=/home/.../Documents/programas/softwares/player/cap-icon.ico
Terminal=false
Name[pt_BR]=player
```

## Eclipse Window Builder

- [WindowBuilder](https://www.eclipse.org/windowbuilder/)



