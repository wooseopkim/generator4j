let
  pkgs = import <nixpkgs> { };
in
with pkgs;
mkShell {
  packages = [
    graalvmPackages.graalvm-ce
    act
    actionlint
    nixfmt-rfc-style
    nil
  ];
}
