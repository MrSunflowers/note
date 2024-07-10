#!/bin/bash

directory="/opt/net/bak"

GRUB_CMDLINE_LINUX_STR="GRUB_CMDLINE_LINUX=\"crashkernel=auto spectre_v2=retpoline net.ifnames=0 biosdevname=0 rd.lvm.lv=centos/root rd.lvm.lv=centos/swap rhgb quiet\""
GRUB_CMDLINE_LINUX_STR2="GRUB_CMDLINE_LINUX=\"crashkernel=auto spectre_v2=retpoline rd.lvm.lv=centos/root rd.lvm.lv=centos/swap rhgb quiet\""


if [ ! -d "$directory" ]; then
    mkdir "$directory"
fi

if [ ! -f "$directory/ifcfg-ens33" ]; then
    cp /etc/sysconfig/network-scripts/ifcfg-ens33 /opt/net/bak/ifcfg-ens33
fi

if [ ! -f "$directory/grub" ]; then
    cp /etc/default/grub /opt/net/bak/grub
fi

if [ -f "/etc/sysconfig/network-scripts/ifcfg-eth0" ]; then
    rm -rf /etc/sysconfig/network-scripts/ifcfg-eth0
fi

sed -i '4s/.*/BOOTPROTO=dhcp/' /etc/sysconfig/network-scripts/ifcfg-ens33
sed -i '15s/.*/ONBOOT=yes/' /etc/sysconfig/network-scripts/ifcfg-ens33

sed -i '16d' /etc/sysconfig/network-scripts/ifcfg-ens33
sed -i '17d' /etc/sysconfig/network-scripts/ifcfg-ens33
sed -i '18d' /etc/sysconfig/network-scripts/ifcfg-ens33
sed -i '19d' /etc/sysconfig/network-scripts/ifcfg-ens33
sed -i '20d' /etc/sysconfig/network-scripts/ifcfg-ens33
sed -i '21d' /etc/sysconfig/network-scripts/ifcfg-ens33

sed -i '6s/.*/GRUB_CMDLINE_LINUX="crashkernel=auto spectre_v2=retpoline rd.lvm.lv=centos\/root rd.lvm.lv=centos\/swap rhgb quiet"/' /etc/default/grub

nmcli c reload
grub2-mkconfig -o /boot/grub2/grub.cfg
