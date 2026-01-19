# Скрипт для управления учетными записями пользователей Active Directory - ADUserManager.ps1

# Импортируем модуль Active Directory
Import-Module ActiveDirectory

# Функция для создания нового пользователя
function New-ADUserFromTemplate {
param (
[Parameter(Mandatory=$true)]
[string]$FirstName,

[Parameter(Mandatory=$true)]
[string]$LastName,

[Parameter(Mandatory=$true)]
[string]$Department,

[Parameter(Mandatory=$true)]
[string]$Title,

[Parameter(Mandatory=$true)]
[string]$Manager,

[Parameter(Mandatory=$false)]
[string]$TemplateUser = "",

[Parameter(Mandatory=$false)]
[string]$OU = "OU=Users,DC=yourdomain,DC=com"
)

# Генерируем имя пользователя (первая буква имени + фамилия)
$username = ($FirstName.Substring(0, 1) + $LastName).ToLower()
$originalUsername = $username
$counter = 1

# Проверяем, существует ли пользователь с таким именем
while (Get-ADUser -Filter "SamAccountName -eq '$username'" -ErrorAction SilentlyContinue) {
$username = $originalUsername + $counter
$counter++
}

# Генерируем случайный пароль
$password = [System.Web.Security.Membership]::GeneratePassword(12, 3)
$securePassword = ConvertTo-SecureString -String $password -AsPlainText -Force

# Создаем базовые параметры для нового пользователя
$userParams = @{
SamAccountName = $username
UserPrincipalName = "$username@yourdomain.com"
Name = "$FirstName $LastName"
GivenName = $FirstName
Surname = $LastName
DisplayName = "$LastName, $FirstName"
Department = $Department
Title = $Title
Manager = (Get-ADUser -Filter "Name -eq '$Manager'" -ErrorAction SilentlyContinue).DistinguishedName
Enabled = $true
ChangePasswordAtLogon = $true
AccountPassword = $securePassword
Path = $OU
}

# Если указан шаблонный пользователь, копируем группы и другие атрибуты
if ($TemplateUser -and (Get-ADUser -Filter "SamAccountName -eq '$TemplateUser'" -ErrorAction SilentlyContinue)) {
try {
# Создаем пользователя
New-ADUser @userParams

# Получаем группы шаблонного пользователя
$templateGroups = Get-ADUser -Identity $TemplateUser -Properties MemberOf | Select-Object -ExpandProperty MemberOf

# Добавляем нового пользователя в те же группы
foreach ($group in $templateGroups) {
Add-ADGroupMember -Identity $group -Members $username
}

# Копируем дополнительные атрибуты (например, адрес, телефон и т.д.)
$templateUser = Get-ADUser -Identity $TemplateUser -Properties *
$newUser = Get-ADUser -Identity $username -Properties *

$attributesToCopy = @("StreetAddress", "City", "State", "PostalCode", "Country", "Company", "Office", "OfficePhone")

foreach ($attribute in $attributesToCopy) {
if ($templateUser.$attribute) {
Set-ADUser -Identity $username -Replace @{$attribute = $templateUser.$attribute}
}
}

Write-Host "Пользователь $username создан на основе шаблона $TemplateUser" -ForegroundColor Green
}
catch {
Write-Host "Ошибка при создании пользователя: $_" -ForegroundColor Red
}
}
else {
try {
# Создаем пользователя без шаблона
New-ADUser @userParams

# Добавляем пользователя в группу "Domain Users" (если еще не добавлен)
Add-ADGroupMember -Identity "Domain Users" -Members $username -ErrorAction SilentlyContinue

Write-Host "Пользователь $username создан" -ForegroundColor Green
}
catch {
Write-Host "Ошибка при создании пользователя: $_" -ForegroundColor Red
}
}

# Возвращаем информацию о созданном пользователе
return @{
Username = $username
Password = $password
Email = "$username@yourdomain.com"
}
}

# Функция для блокировки/разблокировки пользователя
function Set-ADUserStatus {
param (
[Parameter(Mandatory=$true)]
[string]$Username,

[Parameter(Mandatory=$true)]
[bool]$Enabled
)

try {
Set-ADUser -Identity $Username -Enabled $Enabled
$status = if ($Enabled) { "разблокирован" } else { "заблокирован" }
Write-Host "Пользователь $Username $status" -ForegroundColor Green
}
catch {
Write-Host "Ошибка при изменении статуса пользователя: $_" -ForegroundColor Red
}
}

# Функция для перемещения пользователя в другое подразделение
function Move-ADUserToOU {
param (
[Parameter(Mandatory=$true)]
[string]$Username,

[Parameter(Mandatory=$true)]
[string]$TargetOU
)

try {
$user = Get-ADUser -Identity $Username
Move-ADObject -Identity $user.DistinguishedName -TargetPath $TargetOU
Write-Host "Пользователь $Username перемещен в $TargetOU" -ForegroundColor Green
}
catch {
Write-Host "Ошибка при перемещении пользователя: $_" -ForegroundColor Red
}
}

# Функция для создания отчета о неактивных пользователях
function Get-InactiveADUsers {
param (
[Parameter(Mandatory=$false)]
[int]$DaysInactive = 90,

[Parameter(Mandatory=$false)]
[string]$ReportPath = "C:\Reports\InactiveUsers_$(Get-Date -Format 'yyyyMMdd').csv"
)

# Создаем директорию для отчетов, если она не существует
if (!(Test-Path (Split-Path $ReportPath -Parent))) {
New-Item -ItemType Directory -Path (Split-Path $ReportPath -Parent) -Force | Out-Null
}

$inactiveDate = (Get-Date).AddDays(-$DaysInactive)

try {
# Получаем неактивных пользователей
$inactiveUsers = Get-ADUser -Filter {LastLogonTimeStamp -lt $inactiveDate -and Enabled -eq $true} -Properties LastLogonTimeStamp, DisplayName, Department, Title, Manager, whenCreated |
Select-Object SamAccountName, DisplayName, Department, Title,
@{Name="Manager"; Expression={(Get-ADUser -Identity $_.Manager -Properties DisplayName).DisplayName}},
@{Name="LastLogon"; Expression={[DateTime]::FromFileTime($_.LastLogonTimeStamp)}},
@{Name="AccountAge"; Expression={(New-TimeSpan -Start $_.whenCreated -End (Get-Date)).Days}},
@{Name="DaysSinceLastLogon"; Expression={(New-TimeSpan -Start ([DateTime]::FromFileTime($_.LastLogonTimeStamp)) -End (Get-Date)).Days}}

# Экспортируем результаты в CSV
$inactiveUsers | Export-Csv -Path $ReportPath -NoTypeInformation -Encoding UTF8

Write-Host "Найдено $($inactiveUsers.Count) неактивных пользователей (более $DaysInactive дней)" -ForegroundColor Yellow
Write-Host "Отчет сохранен в $ReportPath" -ForegroundColor Green

return $inactiveUsers
}
catch {
Write-Host "Ошибка при создании отчета о неактивных пользователях: $_" -ForegroundColor Red
}
}

# ----- Пример использования функций
# Раскомментируйте нужные строки для выполнения соответствующих операций

# ----- Создание нового пользователя на основе шаблона
# $newUser = New-ADUserFromTemplate -FirstName "Иван" -LastName "Петров" -Department "IT" -Title "Системный администратор" -Manager "Сергей Иванов" -TemplateUser "jsmith"
# Write-Host "Создан пользователь: $($newUser.Username)" -ForegroundColor Green
# Write-Host "Пароль: $($newUser.Password)" -ForegroundColor Green
# Write-Host "Email: $($newUser.Email)" -ForegroundColor Green

# ----- Блокировка пользователя
# Set-ADUserStatus -Username "jsmith" -Enabled $false

# ----- Разблокировка пользователя
# Set-ADUserStatus -Username "jsmith" -Enabled $true

# ----- Перемещение пользователя в другое подразделение
# Move-ADUserToOU -Username "jsmith" -TargetOU "OU=Disabled Users,DC=yourdomain,DC=com"

# ----- Создание отчета о неактивных пользователях
# Get-InactiveADUsers -DaysInactive 60

# ----- Как использовать этот скрипт
# Сохраните скрипт в файл ADUserManager.ps1
# Настройте параметры в скрипте (домен, пути к OU и т.д.)
# Раскомментируйте примеры использования функций в конце скрипта или вызывайте функции из других скриптов
#
# ---Этот скрипт особенно полезен, когда вам нужно:
# * Быстро создать нескольких пользователей с похожими параметрами
# * Массово обрабатывать учетные записи (блокировать/разблокировать, перемещать)
# * Регулярно создавать отчеты о неактивных пользователях
# * Преимущества этого скрипта
# * Автоматизация рутинных операций с учетными записями пользователей
# * Возможность создания пользователей на основе шаблонов
# * Генерация случайных паролей и автоматическое добавление в группы
# * Создание отчетов о неактивных пользователях для аудита безопасности
