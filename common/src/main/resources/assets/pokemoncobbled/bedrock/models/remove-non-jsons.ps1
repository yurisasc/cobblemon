Get-ChildItem . -Recurse | Where{$_.Name -Match ".bbmodel"} | Remove-Item
Get-ChildItem . -Recurse | Where{$_.Name -Match ".animation.json"} | Remove-Item
Get-ChildItem . -Recurse | Where{$_.Name -Match ".png"} | Remove-Item
