Set CURRENTDIR=%CD%
cd "Procesadas"
move "*.jpg" "%CURRENTDIR%\"
cd..
cd "Recortes"
del *
